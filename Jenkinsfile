pipeline {
    agent any
    tools {
        maven 'maven 3.8.4'
    }
    environment {
        APPLICATION_NAME = "full-stack-app"
        STAGE_TAG = "promoteToQA"
        DEV_PROJECT = "dev"
        STAGE_PROJECT = "stage"
        ARTIFACT_FOLDER = "target"
        TEMPLATE_NAME = "full-stack-app"
        PORT = 8081
    }
    stages {
        stage('Build & Package') {
            steps {
                sh 'mvn -B clean package'
            }
        }
    }
}