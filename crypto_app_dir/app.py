from utils import *

app = Flask(__name__)

@app.route('/')
def index():
    data = fetch_crypto_data()
    # Calculate average values
    avg_market_cap, avg_price = calulation(data)
    return render_template('index.html', data=data, avg_market_cap=avg_market_cap, avg_price=avg_price)

@app.route('/top-gainers')
def top_gainers():
    data = fetch_crypto_data()
    top_gainers = sorted(data, key=lambda x: x['price_change_percentage_24h'], reverse=True)[:10]
    return render_template('top_gainers.html', data=top_gainers)

@app.route('/top-losers')
def top_losers():
    data = fetch_crypto_data()
    top_losers = sorted(data, key=lambda x: x['price_change_percentage_24h'])[:10]
    return render_template('top_losers.html', data=top_losers)

@app.route('/search', methods=['GET', 'POST'])
def search():
    if request.method == 'POST':
        coin_name = request.form.get('coin_name', '').strip()  # Get the coin name and strip any extra spaces
        if not coin_name:
            # If the coin_name is empty, return to the search page with an error message
            return render_template('search.html', error="Please enter a valid coin name.")
        print(f"Searching for coin: {coin_name}")
        # Redirect to the coin_detail route
        return redirect(url_for('coin_detail', coin_name=coin_name))
    return render_template('search.html')

@app.route('/coin/<coin_name>')
def coin_detail(coin_name):
    try:
        # Fetch the list of all coins from the cache or API
        coins_list = get_coin_lists()
        coin_name = coin_name.lower()
        # Find the coin that matches the user's input
        matching_coin = next((coin for coin in coins_list if coin_name in [coin['id'], coin['symbol'].lower(), coin['name'].lower()]), None)
        if not matching_coin:
            return render_template('error.html', message="Coin not found. Please check the name and try again.")

        # Fetch detailed information about the matched coin
        coin = get_coin_details(matching_coin['id'])
        return render_template('coin_detail.html', coin=coin)

    except requests.exceptions.HTTPError as http_err:
        return render_template('error.html', message=f"HTTP error occurred: {http_err}")
    except Exception as err:
        return render_template('error.html', message=f"An error occurred: {err}")

@app.route('/download-csv')
def download_csv():
    try:
        # Fetch the cryptocurrency data
        data = fetch_crypto_data()
        if not data:
            raise ValueError("No data available to download.")
        # Create a CSV file in memory
        si = io.StringIO()
        writer = csv.writer(si)
        # Writ header
        writer.writerow(['ID', 'Symbol', 'Name', 'Current Price (USD)', 'Market Cap (USD)', '24h Change (%)'])
        # Write data
        for coin in data:
            writer.writerow([
                coin['id'],
                coin['symbol'],
                coin['name'],
                coin['current_price'],
                coin['market_cap'],
                coin['price_change_percentage_24h']
            ])
        # Create a Flask response with the CSV data
        output = make_response(si.getvalue())
        output.headers['Content-Disposition'] = 'attachment; filename=cryptocurrencies.csv'
        output.headers['Content-type'] = 'text/csv'
        return output
    except Exception as e:
        print(f"An error occurred: {e}")
        return render_template('error.html', message="An error occurred while generating the CSV file.")

if __name__ == '__main__':
    app.run(debug=True)