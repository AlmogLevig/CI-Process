data "aws_vpc" "selected" {
  filter {
    name            = "isDefault"
    values          = ["true"]
  }
}

data "aws_subnets" "selected" {
    filter {
    name   = "vpc-id"
    values = [data.aws_vpc.selected.id]
  }
}

resource "aws_eip" "test_ec2_eip" {
  domain = "vpc"
  instance = aws_instance.test_ec2.id
}