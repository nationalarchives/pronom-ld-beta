# Set up AWS ECR Repo for the backend image
resource "aws_ecr_repository" "pronom-backend-repo" {
  name                 = "pronom-backend"
  image_tag_mutability = "MUTABLE"
}

resource "aws_ecr_repository_policy" "pronom-backend-repo-policy" {
  repository = aws_ecr_repository.pronom-backend-repo.name
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
    value = aws_ecr_repository.pronom-backend-repo.repository_url
}
