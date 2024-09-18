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

# In case of VIP that already exist
# resource "aws_eip_association" "test_ec2_eip" {
#   instance_id   = aws_instance.test_ec2.id
#   allocation_id = var.vip  
# }