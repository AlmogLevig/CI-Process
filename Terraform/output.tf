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