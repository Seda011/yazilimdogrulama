pipeline {
    agent any

    environment {
        // App URL for System Tests
        APP_URL = "http://localhost:8080"
    }

    tools {
        maven 'Maven 3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Stage 1: Checkout (Done automatically by Jenkins SCM but stating explicitly)'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Stage 2: Build'
                // Skip tests here to just compile and package
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Stage 3: Unit Tests'
                bat 'mvn test -Dtest=*ServiceTest'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Stage 4: Integration Tests'
                bat 'mvn verify -Dtest=*IT'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Deploy (Docker)') {
            steps {
                echo 'Stage 5: Deploy to Docker'
                bat 'docker-compose down'
                bat 'docker-compose up -d --build'
                
                // Wait for the application to be ready (naive wait)
                sleep 20
            }
        }

        stage('System Tests (Selenium)') {
            steps {
                echo 'Stage 6: System/Selenium Tests'
                // Run only the system tests, passing the APP_URL
                bat 'mvn test -Dtest=SeleniumSystemTest -Dapp.url=%APP_URL%'
            }
            post {
                always {
                    // System tests run with surefire (mvn test), so they output to surefire-reports
                    // We capture them again/additively
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline Execution Finished'
        }
    }
}
