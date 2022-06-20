pipeline {
    agent {
      node {
          label 'master'
       }
    }
    options { timeout(time: 4, unit: 'MINUTES') }
    stages {
        stage('Pull') {
            options{
                timeout(time:20,unit:'SECONDS')
            }
            steps {
                echo 'Pulling..'
                git 'https://gitee.com/pgh1038/gobang.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                sh label: 'mvn构建', script: 'mvn clean && mvn install'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                jacoco(
                buildOverBuild: false,
                changeBuildStatus: true,
                classPattern: '**/target/classes/com',
                execPattern: '**/target/coverage-reports/jacoco-ut.exec',
                sourcePattern: '**/app',
                exclusionPattern: '**/repositories/**,**/ForecastDealListTopic*,**/RedisProxy,**/SqlProvider,**/javascript/**,**/Reverse*,**/routes*,**/*$*,**/RedisConnector,**/RedisProxy,**/RedisUtil*,**/dao/**,**/OAuthTokenVerification*,**/dbpool/**,**/module/**,**/modules/**',
                minimumMethodCoverage: '0',
                maximumMethodCoverage: '0',
                minimumClassCoverage: '0',
                maximumClassCoverage: '0',
                minimumLineCoverage: '0',
                maximumLineCoverage: '0'
              )
            }
        }
        // stage('Deploy jar') {
        //     steps {
        //         sshPublisher(publishers: [sshPublisherDesc(configName: '119.91.143.195', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/', remoteDirectorySDF: false, removePrefix: 'target', sourceFiles: 'target/*.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
        //         sshPublisher(publishers: [sshPublisherDesc(configName: '119.91.143.195', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'JENKINS_NODE_COOKIE=dontkillme sh pipeline_node.sh restart', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'pipeline_node.sh')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
        //     }
        // }
        stage('Run') {
            steps{
                sh"""JENKINS_NODE_COOKIE=dontkillme sh fivechess.sh restart """
            }
        }
        stage('Send Email'){
            steps {
                emailext body: '${DEFAULT_CONTENT}', mimeType: 'text/html', subject: '自动化构建通知：${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${BUILD_STATUS}!', to: '${DEFAULT_RECIPIENTS}'
            }
        }
    }
}