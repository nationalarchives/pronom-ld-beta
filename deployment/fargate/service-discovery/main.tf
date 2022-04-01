resource "aws_service_discovery_private_dns_namespace" "discovery" {
  name        = "local"
  description = "pronom-discovery-service"
  vpc         = var.vpc_id
}


resource "aws_service_discovery_service" "discovery-backend" {
  name = "backend"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.discovery.id

    dns_records {
      ttl  = 10
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}


resource "aws_service_discovery_service" "discovery-triplestore" {
  name = "triplestore"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.discovery.id

    dns_records {
      ttl  = 10
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

output "discovery_backend_arn" {
  value = aws_service_discovery_service.discovery-backend.arn
}

output "discovery_triplestore_arn" {
  value = aws_service_discovery_service.discovery-triplestore.arn
}
