variable "name" {
  description = "the name of your stack, e.g. \"demo\""
}

variable "environment" {
  description = "the name of your environment, e.g. \"prod\""
}

variable "subnets" {
  description = "The public subnets to pass to the EFS configuration"
}

variable "security_group" {
  description = "the name of th security group to use for the EFS"
}