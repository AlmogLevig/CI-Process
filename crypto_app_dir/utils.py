from flask import Flask, render_template, request, redirect, url_for, make_response
import time
import requests
import csv
import io

_coin_list_cache = None
_cache_timestamp = None
_cache_duration = 360  # Cache duration in seconds

def get_coin_lists(): 
    """
    Retrieves the list of all available cryptocurrencies.
    This function checks if the coin list is cached and still validist.
    Returns:
        list: A list of all available cryptocurrencies from the API.
    """
    global _coin_list_cache, _cache_timestamp

    if _coin_list_cache and (time.time() - _cache_timestamp) < _cache_duration: # Check if the cache is still valid
        return _coin_list_cache
    
    # Fetch the list of all coins from the API
    _coin_list_cache = api_call("list")
    _cache_timestamp = time.time()
    
    return _coin_list_cache


def get_coin_details(matching_coin): 
    """
    Fetches detailed information about a specific cryptocurrency.

    This function takes the identifier of a cryptocurrency and makes an API call 
    to retrieve detailed information about that coin.

    Args:
        matching_coin (str): The identifier of the cryptocurrency to fetch details for.

    Returns:
        dict: A dictionary containing detailed information about the specified cryptocurrency.
    """
    coin_response = api_call(matching_coin)
    return coin_response


def fetch_crypto_data():
    """
    Fetches cryptocurrency market data.

    This function retrieves data for the top 100 cryptocurrencies by market capitalization in USD.
    It makes an API call to fetch this data with the following parameters:
    - `vs_currency`: The currency to which the cryptocurrency prices are converted (default: USD).
    - `order`: The order of the cryptocurrencies based on market capitalization in descending order.
    - `per_page`: The number of results per page (default: 100).
    - `page`: The page number to retrieve (default: 1).
    - `sparkline`: Indicates whether to include sparkline data (default: False).

    Returns:
        response (dict or list): The response data from the API containing cryptocurrency market information.
    """
    parameters = {
        "vs_currency": "usd",
        "order": "market_cap_desc",
        "per_page": 100, 
        "page": 1,
        "sparkline": "false"
    }
    response = api_call("markets", params=parameters)
    return response


def api_call(operation, params=None):
    """
    A generic function to make API calls to the CoinGecko API.
    
    Args:
        operation (str): The operation or endpoint to be appended to the base URL.
        params (dict): The parameters to be passed to the API call.

    Returns:
        dict: The JSON response from the API call.
    """
    base_url = "https://api.coingecko.com/api/v3/coins/"
    url = f"{base_url}{operation}"
    response = requests.get(url, params=params)
    response.raise_for_status()  # Will raise an exception for 4xx/5xx responses
    return response.json()  # Returns the response content in JSON format


def calulation(data):
    """
    Calculates average market capitalization and price for a list of cryptocurrencies.
    Args:
        data (list of dict): A list of dictionaries where each dictionary contains details 
                             of a cryptocurrency, including 'market_cap' and 'current_price'.
    Returns:
        tuple: A tuple containing the average market capitalization and average price 
               (avg_market_cap, avg_price).
    """
    total_market_cap, total_price = (
            sum(coin[key] for coin in data) for key in ['market_cap', 'current_price'])
    
    avg_market_cap, avg_price = total_market_cap / len(data), total_price / len(data)

    return avg_market_cap, avg_price