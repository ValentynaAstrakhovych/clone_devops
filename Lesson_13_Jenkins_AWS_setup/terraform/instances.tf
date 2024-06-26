resource "aws_instance" "jenkins" {
  ami                    = "ami-053b0d53c279acc90" # Ubuntu server 22.04
  instance_type          = "t2.micro"              # "c6a.large" # Do not forget to turn off the instance after the test (56$ per month)
  vpc_security_group_ids = [aws_security_group.web-sg.id]
  key_name               = "lesson_7_ansible"
  iam_instance_profile   = aws_iam_instance_profile.jenkins_profile.name

  tags = {
    Name = "Jenkins_DevOps_Group_Jan_24"
  }
}

