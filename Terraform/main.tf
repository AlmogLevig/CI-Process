resource "aws_instance" "test_ec2" {
    ami             = var.ami
    instance_type   = var.instance_type 
    subnet_id       = data.aws_subnets.selected.ids[0]

    # vpc_security_group_ids = [aws_security_group.test_ec2_sg.id]
    associate_public_ip_address = true

    user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y httpd
    systemctl start httpd
    systemctl enable httpd
    EOF

    tags = {
      Name = "Test EC2 with Apache"
    }

}