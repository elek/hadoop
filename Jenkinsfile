pipeline {
    agent { docker 'maven:3-alpine' } 
    stages {
        stage('Verify') {
            steps {
                sh 'mvn -B clean verify'
            }
        }
    }
}
