output "test_ec2_private_ip" {
  value = aws_instance.test_ec2.private_ip
}
output "test_ec2_public_ip" {
  value = aws_instance.test_ec2.public_ip
}
output "url" {
  value = "http://${aws_instance.test_ec2.public_ip}:80"
}