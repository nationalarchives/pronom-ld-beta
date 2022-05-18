variable "aws_region" {
  type        = string
  description = "AWS region to launch servers."
  default     = "eu-west-2"
}

variable "aws_profile" {
  type        = string
  description = "AWS profile to load."
  default     = "pronomdev"
}

variable "aws_secret_id" {
  type        = string
  description = "AWS secret ID."
}

variable "aws_secret_key" {
  type        = string
  description = "AWS secret key."
}

variable "aws_assume_role" {
  type        = string
  description = "IAM role to assume"
}

variable "ssh_pub_key" {
  type        = string
  description = "Path to the ssh key for SSH access"
}