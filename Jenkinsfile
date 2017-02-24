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
                sh 'mvn test || true'
                junit '**/target/**/*.xml'
            }
        }
        stage('build') {
            steps {
                sh 'mvn package'
            }
        }
        stage('install') {
            steps {
                sh 'mvn install'
            }
        }
        stage('deploy') {
            steps {
                sh 'mvn deploy'
            }
        }
    }
}