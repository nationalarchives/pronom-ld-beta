variable "name" {
  description = "the name of your stack, e.g. \"demo\""
}

variable "environment" {
  description = "the name of your environment, e.g. \"prod\""
}

variable "vpc_id" {
  description = "The VPC ID"
}

variable "vpc_cidr" {
  description = "The VPC CIDR block"
}

variable "container_port" {
  description = "Ingress and egress port of the container"
}
