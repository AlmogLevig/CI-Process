# variables.tf
variable "my_ip" {
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

variable "test_tag" {
  type        = string
}

variable "leumi_ip" {
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

variable "vip" {
  description = "Elastic IP"
  type        = string
}