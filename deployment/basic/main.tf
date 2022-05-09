provider "aws" {
  region     = var.aws_region
  profile    = var.aws_profile
  access_key = var.aws_secret_id
  secret_key = var.aws_secret_key
  assume_role {
    role_arn = var.aws_assume_role
  }
}

resource "aws_instance" "ec2_pronom" {
  ami                         = "ami-0a244485e2e4ffd03"
  instance_type               = "t3.large"
  vpc_security_group_ids      = [aws_security_group.main.id]
  associate_public_ip_address = true
  key_name                    = "pronom"

  lifecycle {
    ignore_changes = [ami]
  }
}

output "instance_ip" {
  description = "The public ip for ssh access"
  value       = aws_instance.ec2_pronom.public_ip
}

resource "aws_security_group" "main" {
  egress = [
    {
      cidr_blocks      = ["0.0.0.0/0", ]
      description      = ""
      from_port        = 0
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "-1"
      security_groups  = []
      self             = false
      to_port          = 0
    }
  ]
  ingress = [
    {
      cidr_blocks      = ["0.0.0.0/0", ]
      description      = ""
      from_port        = 22
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "tcp"
      security_groups  = []
      self             = false
      to_port          = 22
    },
    {
      cidr_blocks      = ["0.0.0.0/0", ]
      description      = ""
      from_port        = 80
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      protocol         = "tcp"
      security_groups  = []
      self             = false
      to_port          = 80
    }
  ]
}
