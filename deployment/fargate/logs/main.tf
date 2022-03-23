resource "aws_cloudwatch_log_group" "main" {
  name = "${var.name}-cloudwatch-log-group"
  retention_in_days = 14
  tags = {
    Name        = "${var.name}-cloudwatch-log-group"
    Environment = var.environment
  }
}

output "arn" {
  value = aws_cloudwatch_log_group.main.arn
}

output "name" {
  value = aws_cloudwatch_log_group.main.name
}