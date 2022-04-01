variable "name" {
  description = "the name of your stack, e.g. \"demo\""
}

variable "environment" {
  description = "the name of your environment, e.g. \"prod\""
}

variable "region" {
  description = "the AWS region in which resources are created"
}

variable "subnets" {
  description = "List of subnet IDs"
}

variable "ecs_service_security_groups" {
  description = "Comma separated list of security groups"
}

variable "backend_port" {
  description = "Port of container"
}

variable "backend_image" {
  description = "Docker image to be launched for the backend service"
}

variable "backend_tag" {
  description = "Docker image tag for the backend service"
}

variable "triplestore_image" {
  description = "Docker image to be launched for the triplestore service"
}

variable "triplestore_tag" {
  description = "Docker image tag for the triplestore service"
}

variable "aws_alb_target_group_arn" {
  description = "ARN of the alb target group"
}

variable "log_name" {
  description = "Name of the log group"
}

variable "service_desired_count" {
  description = "Number of services running in parallel"
}

variable "backend_environment" {
  description = "The container environmnent variables"
  type        = list(any)
}
variable "triplestore_environment" {
  description = "The triplestore environmnent variables"
  type        = list(any)
}

variable "discovery_backend_arn" {
  description = "The arn of the backend discovery service"
  type        = string
}

variable "discovery_triplestore_arn" {
  description = "The arn of the triplestore discovery service"
  type        = string
}

variable "container_secrets" {
  description = "The container secret environmnent variables"
  type        = list(any)
}

variable "container_secrets_arns" {
  description = "ARN for secrets"
}

variable "backend_md_efs_id" {
  description = "EFS ID for the backend Markdown directory"
}

variable "backend_md_efs_name" {
  description = "EFS Name for the backend Markdown directory"
}
