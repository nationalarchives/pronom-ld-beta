# PRONOM Deployment definition

This project defines a deployment strategy using Terraform as the insfrastructure-as-code management solution.

## Structure

```bash
.
├── app
│   ├── ecs.tf
│   ├── iam.tf
│   ├── main.tf
│   └── vpc.tf
├── ecr
│   ├── ecr.tf
│   └── main.tf
└── README.md

```

The deployment is split into 2 different definitions as they should be controlled independently.

### ECR - Elastic Container Registry

This is a very simple collection of docker image repositories hoested on AWS. The reason why it is separate is because it does not need to be created/destroyed alongside the application and by keeping them separate we guarantee that the images are still in the repository even if the insfrastructure for the application is taken down.

### Application

This is the main