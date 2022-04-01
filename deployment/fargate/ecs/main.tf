resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.name}-ecsTaskExecutionRole"

  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Action" : "sts:AssumeRole",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Effect" : "Allow",
        "Sid" : ""
      }
    ]
  })
}

resource "aws_iam_role" "ecs_task_role" {
  name = "${var.name}-ecsTaskRole"

  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Action" : "sts:AssumeRole",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Effect" : "Allow",
        "Sid" : ""
      }
    ]
  })
}


resource "aws_iam_role_policy_attachment" "ecs-task-execution-role-policy-attachment" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_cluster" "main" {
  name = "${var.name}-cluster-${var.environment}"
  tags = {
    Name        = "${var.name}-cluster-${var.environment}"
    Environment = var.environment
  }
}

# Backend Task definition and service
resource "aws_ecs_task_definition" "backend" {
  family                   = "${var.name}-task-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn
  container_definitions = jsonencode([{
    name      = "${var.name}-container-${var.environment}-backend"
    image     = "${var.backend_image}:${var.backend_tag}"
    essential = true
    mountPoints = [{
      containerPath = "/md"
      sourceVolume  = "markdown"
    }]
    environment = var.backend_environment
    portMappings = [{
      protocol      = "tcp"
      containerPort = var.backend_port
      hostPort      = var.backend_port
    }]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = var.log_name
        awslogs-stream-prefix = "ecs"
        awslogs-region        = var.region
      }
    }
  }])

  volume {
    name = "markdown"
    efs_volume_configuration {
      file_system_id = var.backend_md_efs_id
      root_directory = "/"
    }
  }

  tags = {
    Name        = "${var.name}-task-${var.environment}-backend"
    Environment = var.environment
  }
}

resource "aws_ecs_service" "backend" {
  name                               = "${var.name}-service-${var.environment}-backend"
  cluster                            = aws_ecs_cluster.main.id
  task_definition                    = aws_ecs_task_definition.backend.arn
  desired_count                      = var.service_desired_count
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  health_check_grace_period_seconds  = 60
  launch_type                        = "FARGATE"
  scheduling_strategy                = "REPLICA"
  force_new_deployment               = true

  network_configuration {
    security_groups  = var.ecs_service_security_groups
    subnets          = var.subnets.*.id
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.aws_alb_target_group_arn
    container_name   = "${var.name}-container-${var.environment}-backend"
    container_port   = var.backend_port
  }

  service_registries {
    registry_arn   = var.discovery_backend_arn
    container_name = "backend"
  }

  # we ignore task_definition changes as the revision changes on deploy
  # of a new version of the application
  # desired_count is ignored as it can change due to autoscaling policy
  lifecycle {
    ignore_changes = [desired_count]
  }
  depends_on = [var.log_name]
}

resource "aws_appautoscaling_target" "ecs_target_backend" {
  max_capacity       = 4
  min_capacity       = 1
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.backend.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}


resource "aws_appautoscaling_policy" "ecs_policy_memory_backend" {
  name               = "memory-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target_backend.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target_backend.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target_backend.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }

    target_value       = 80
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}

resource "aws_appautoscaling_policy" "ecs_policy_cpu_backend" {
  name               = "cpu-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target_backend.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target_backend.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target_backend.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }

    target_value       = 60
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}

# Triplestore task definition and service

resource "aws_ecs_task_definition" "triplestore" {
  family                   = "${var.name}-task-${var.environment}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 2048
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn
  container_definitions = jsonencode([{
    name      = "${var.name}-container-${var.environment}-triplestore"
    image     = "${var.triplestore_image}:${var.triplestore_tag}"
    essential = true
    # mountPoints = [{
    #   containerPath = "/fuseki-base/databases"
    #   sourceVolume  = "database"
    # }]
    environment = var.triplestore_environment
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = var.log_name
        awslogs-stream-prefix = "ecs"
        awslogs-region        = var.region
      }
    }
  }])

  # volume {
  #   name = "database"
  #   efs_volume_configuration {
  #     file_system_id = var.triplestore_efs_id
  #     root_directory = "/"
  #   }
  # }

  tags = {
    Name        = "${var.name}-task-${var.environment}-triplestore"
    Environment = var.environment
  }
}

resource "aws_ecs_service" "triplestore" {
  name                               = "${var.name}-service-${var.environment}-triplestore"
  cluster                            = aws_ecs_cluster.main.id
  task_definition                    = aws_ecs_task_definition.triplestore.arn
  desired_count                      = var.service_desired_count
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  launch_type                        = "FARGATE"
  scheduling_strategy                = "REPLICA"
  force_new_deployment               = true

  network_configuration {
    security_groups  = var.ecs_service_security_groups
    subnets          = var.subnets.*.id
    assign_public_ip = false
  }

  service_registries {
    registry_arn   = var.discovery_triplestore_arn
    container_name = "triplestore"
  }

  # we ignore task_definition changes as the revision changes on deploy
  # of a new version of the application
  # desired_count is ignored as it can change due to autoscaling policy
  lifecycle {
    ignore_changes = [desired_count]
  }
  depends_on = [var.log_name]
}

resource "aws_appautoscaling_target" "ecs_target_triplestore" {
  max_capacity       = 4
  min_capacity       = 1
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.triplestore.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

resource "aws_appautoscaling_policy" "ecs_policy_memory_triplestore" {
  name               = "memory-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target_triplestore.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target_triplestore.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target_triplestore.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }

    target_value       = 80
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}

resource "aws_appautoscaling_policy" "ecs_policy_cpu_triplestore" {
  name               = "cpu-autoscaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target_triplestore.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target_triplestore.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target_triplestore.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }

    target_value       = 60
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}
