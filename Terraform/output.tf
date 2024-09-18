output "test_ec2_private_ip" {
  value = aws_instance.test_ec2.private_ip
}

output "url" {
  value = "http://${aws_eip.test_ec2_eip.public_ip}:80"
}


# In case of VIP that already exist
# output "url" {
#   value = "http://${aws_eip_association.test_ec2_eip.public_ip}:80"
# }


output "nlb_ip_addresses" {
  value = aws_lb.test_ec2_nlb.ip_address_type
  description = "IP Address type of nlb"
}

output "nlb_dns_name" {
  value = aws_lb.test_ec2_nlb.dns_name
  description = "The DNS of nlb"
}

output "nlb_zone_id" {
  value = aws_lb.test_ec2_nlb.zone_id
  description = "The Zone ID of nlb"
}
