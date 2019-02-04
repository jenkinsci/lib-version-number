pipeline {
    agent none
    stages {
        stage('Parallel Stage') {
            failFast true
            parallel {
                stage('Branch A') {
                    agent {
                        label "linux"
                    }
                    steps {
                        sh 'mvn -B clean verify'
                    }
                }
                stage('Branch B') {
                    agent {
                        label "windows"
                    }
                    steps {
                        bat 'mvn -B clean verify'
                    }
                }
            }
        }
    }
}
