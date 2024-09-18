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