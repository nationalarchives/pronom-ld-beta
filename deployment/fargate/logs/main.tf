resource "aws_cloudwatch_log_group" "main" {
  name = "${var.name}-cloudwatch-log-group"
}

output "arn" {
  value = aws_cloudwatch_log_group.main.arn
}

output "name" {
  value = aws_cloudwatch_log_group.main.name
}