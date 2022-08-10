variable "repositories" {
  default = ["pronom-backend", "pronom-triplestore", "pronom-keycloak"]
}

# Set up AWS ECR Repo for the backend image
resource "aws_ecr_repository" "repo" {
  count                = length(var.repositories)
  name                 = var.repositories[count.index]
  image_tag_mutability = "MUTABLE"
}

resource "aws_ecr_repository_policy" "repo-policy" {
  count      = length(aws_ecr_repository.repo[*])
  repository = aws_ecr_repository.repo[count.index].name
  policy = jsonencode({
    "Version" : "2008-10-17",
    "Statement" : [
      {
        "Sid" : "adds full ecr access to the pronom repository",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:CompleteLayerUpload",
          "ecr:GetDownloadUrlForLayer",
          "ecr:GetLifecyclePolicy",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart"
        ]
      }
    ]
  })
}

output "aws_ecr_repository_url" {
  value = aws_ecr_repository.repo[*].repository_url
}
