resource "aws_instance" "test_ec2" {
    ami                         = var.ami
    instance_type               = var.instance_type 
    key_name                    = var.ssh_key

    subnet_id                   = data.aws_subnets.selected.ids[0]
    vpc_security_group_ids      = [aws_security_group.test_ec2_sg.id]
    associate_public_ip_address = false

    user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y httpd
    systemctl start httpd
    systemctl enable httpd
    EOF

    tags = {
      Name = "${var.test_tag}"
    }
}

resource "aws_lb" "test_ec2_nlb" {
  name               = "test-ec2-nlb"
  internal           = false
  load_balancer_type = "network"
  subnets            = data.aws_subnets.selected.ids

  enable_deletion_protection = false

  tags = {
    Name = "${var.test_tag}_nlb"
  }
}