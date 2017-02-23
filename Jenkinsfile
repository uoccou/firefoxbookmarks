pipeline {
    agent any
    stages {
        stage('version') {
            steps {
                sh 'mvn --version'
            }
        }
        stage('compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('build') {
            steps {
                sh 'mvn package'
            }
        }
    }
}