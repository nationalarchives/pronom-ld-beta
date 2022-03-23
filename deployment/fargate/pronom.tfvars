name                  = "pronom"
environment           = "dev"
region                = "eu-west-2"
availability_zones    = ["eu-west-2a", "eu-west-2b"]
private_subnets       = ["10.0.0.0/20", "10.0.32.0/20"]
public_subnets        = ["10.0.16.0/20", "10.0.48.0/20"]
tsl_certificate_arn   = "arn:aws:acm:eu-west-2:955621375565:certificate/3f6df99d-7ec0-41b8-9be5-caab2c500738" # created manually on AWS console
container_memory      = 512
container_image       = "955621375565.dkr.ecr.eu-west-2.amazonaws.com/pronom-backend:latest" # previously pushed to separately maintained ECR repository
container_port        = 80
service_desired_count = 1
health_check_path     = "/"