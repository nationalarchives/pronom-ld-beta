resource "aws_efs_file_system" "backend_md" {
  creation_token   = "${var.name}-backend-md-${var.environment}"
  performance_mode = "generalPurpose"
  throughput_mode  = "bursting"

  tags = {
    Name = "${var.name}-backend-md-${var.environment}"
  }
}

resource "aws_efs_mount_target" "backend_md_mount" {
  count           = length(var.subnets.*.id)
  file_system_id  = aws_efs_file_system.backend_md.id
  subnet_id       = element(var.subnets.*.id, count.index)
  security_groups = ["${var.security_group}"]
}

output "backend_md_id" {
  value = aws_efs_file_system.backend_md.id
}

output "backend_md_name" {
  value = "${var.name}-backend-md-${var.environment}"
}
