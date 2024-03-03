data "aws_vpc" "main" {
  default = true
}

data "aws_subnets" "vpcsubnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.main.id]
  }
  filter {
    name   = "default-for-az"
    values = [true]
  }
  filter {
    name   = "state"
    values = ["available"]
  }
}

data "aws_subnet" "vpcsubnet" {
  for_each = { for index, subnetid in data.aws_subnets.vpcsubnets.ids : index => subnetid }
  id       = each.value
}

data "aws_caller_identity" "current" {}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }
  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }
  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

# Fetch the Route 53 Hosted Zone ID
# data "aws_route53_zone" "paxel_ca" {
#   name         = "Your_Domain_Name" # Replace with your domain name, ending with a period
#   private_zone = false
# }

# output "hosted_zone_id" {
#   value = data.aws_route53_zone.paxel_ca.zone_id
# }
