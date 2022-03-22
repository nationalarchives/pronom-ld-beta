provider "aws" {
  region = var.region
}

module "logs" {
  source = "./logs"
  name   = var.name
}

# terraform {
#   backend "s3" {
#     bucket  = "terraform-backend-store"
#     encrypt = true
#     key     = "terraform.tfstate"
#     region  = "eu-central-1"
#     # dynamodb_table = "terraform-state-lock-dynamo" - uncomment this line once the terraform-state-lock-dynamo has been terraformed
#   }
# }

# resource "aws_dynamodb_table" "dynamodb-terraform-state-lock" {
#   name           = "terraform-state-lock-dynamo"
#   hash_key       = "LockID"
#   read_capacity  = 20
#   write_capacity = 20
#   attribute {
#     name = "LockID"
#     type = "S"
#   }
#   tags = {
#     Name = "DynamoDB Terraform State Lock Table"
#   }
# }

module "vpc" {
  source             = "./vpc"
  name               = var.name
  cidr               = var.cidr
  private_subnets    = var.private_subnets
  public_subnets     = var.public_subnets
  availability_zones = var.availability_zones
  environment        = var.environment
  log_arn            = module.logs.arn
}

module "security_groups" {
  source         = "./security-groups"
  name           = var.name
  vpc_id         = module.vpc.id
  environment    = var.environment
  container_port = var.container_port
}

module "alb" {
  source              = "./alb"
  name                = var.name
  vpc_id              = module.vpc.id
  subnets             = module.vpc.public_subnets
  environment         = var.environment
  alb_security_groups = [module.security_groups.alb]
  alb_tls_cert_arn    = var.tsl_certificate_arn
  health_check_path   = var.health_check_path
}

module "ecs" {
  source                      = "./ecs"
  name                        = var.name
  environment                 = var.environment
  region                      = var.aws-region
  subnets                     = module.vpc.private_subnets
  aws_alb_target_group_arn    = module.alb.aws_alb_target_group_arn
  ecs_service_security_groups = [module.security_groups.ecs_tasks]
  log_name                    = module.logs.name
  container_port              = var.container_port
  container_cpu               = var.container_cpu
  container_memory            = var.container_memory
  container_image             = var.container_image
  service_desired_count       = var.service_desired_count
  container_secrets_arns      = ""
  container_secrets           = []
  container_environment = [
    { name = "LOG_LEVEL", value = "DEBUG" },
    { name = "PORT", value = var.container_port }
  ]
}
