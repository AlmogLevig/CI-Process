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

resource "aws_lb_target_group" "test_ec2_tg" {
  name     = "test-ec2-tg"
  port     = 80
  protocol = "TCP"
  vpc_id   = data.aws_vpc.selected.id

  health_check {
    protocol            = "HTTP"
    path                = "/"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }
  tags = {
    Name = "${var.test_tag}_tg"
  }
}

resource "aws_lb_target_group_attachment" "test_ec2_tg_attachment" {
  target_group_arn = aws_lb_target_group.test_ec2_tg.arn
  target_id        = aws_instance.test_ec2.id
  port             = 80
}

resource "aws_lb_listener" "test_ec2_listener" {
  load_balancer_arn = aws_lb.test_ec2_nlb.arn
  port              = 80
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.test_ec2_tg.arn
  }
}
