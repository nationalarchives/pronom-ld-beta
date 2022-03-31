aws_profile        = "pronomdev"
name               = "pronom"
environment        = "dev"
region             = "eu-west-2"
availability_zones = ["eu-west-2a", "eu-west-2b"]
private_subnets    = ["10.0.0.0/20", "10.0.32.0/20"]
public_subnets     = ["10.0.16.0/20", "10.0.48.0/20"]
# wallscope certificate
#tsl_certificate_arn   = "arn:aws:acm:eu-west-2:955621375565:certificate/3f6df99d-7ec0-41b8-9be5-caab2c500738" # created manually on AWS console
# pronomdev certificate
tsl_certificate_arn = "arn:aws:acm:eu-west-2:977490479318:certificate/79e2b870-bed2-474d-8812-2212bdb247b9"
# wallscope image
#backend_image       = "955621375565.dkr.ecr.eu-west-2.amazonaws.com/pronom-backend" # previously pushed to separately maintained ECR repository
# pronomdev image
backend_image         = "977490479318.dkr.ecr.eu-west-2.amazonaws.com/pronom-backend"
backend_tag           = "latest"
backend_port          = 80
triplestore_image     = "977490479318.dkr.ecr.eu-west-2.amazonaws.com/pronom-triplestore"
triplestore_tag       = "0.0.0"
service_desired_count = 1
health_check_path     = "/"
