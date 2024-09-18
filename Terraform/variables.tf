# variables.tf
variable "my_ip" {
  description = "Your public IP address"
  type        = string
}

variable "aws_region" {
  description = "region"
  type        = string
}

variable "ami" {
  description = "AL2 AMI"
  type        = string
}

variable "instance_type" {
  description = "t2.micro"
  type        = string
}

variable "ssh_key" {
  description = "SSH key"
  type        = string
}

variable "key_path" {
  type        = string
}

variable "aws_profile" {
  description = "The AWS profile to use"
  type        = string
  default     = "default"
}