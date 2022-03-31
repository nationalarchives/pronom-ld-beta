provider "aws" {
  region     = var.region
  profile    = var.aws_profile
  access_key = var.aws_secret_id
  secret_key = var.aws_secret_key
  assume_role {
    role_arn = var.assume_role
  }
}

module "logs" {
  source      = "./logs"
  name        = var.name
  environment = var.environment
}

# terraform {
#   backend "s3" {
#     bucket  = "terraform-backend-store"
#     encrypt = true
#     key     = "terraform.tfstate"
#     region  = "eu-west-2"
#     # dynamodb_table = "terraform-state-lock-dynamo" - uncomment this line once the terraform-state-lock-dynamo has been terraformed
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
  container_port = var.backend_port
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
  backend_md_efs_id           = module.efs.backend_md_id
  backend_md_efs_name         = module.efs.backend_md_name
  backend_port                = var.backend_port
  backend_image               = var.backend_image
  backend_tag                 = var.backend_tag
  triplestore_image           = var.triplestore_image
  triplestore_tag             = var.triplestore_tag
  service_desired_count       = var.service_desired_count
  container_secrets_arns      = ""
  container_secrets           = []
  backend_environment = [
    { name = "LOG_LEVEL", value = "DEBUG" },
    { name = "PORT", value = var.backend_port }
  ]
}

module "efs" {
  source         = "./efs"
  name           = var.name
  environment    = var.environment
  security_group = module.security_groups.ecs_tasks
  subnets        = module.vpc.private_subnets
}
