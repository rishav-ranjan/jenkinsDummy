def version = "1.0.4"
def mvnpath = "/opt/apache-maven-3.2.5/bin/mvn"
//def url = "https://github.com/WPPg2/DevOps-Deployment"
url = "https://github.com/rishav8/jenkinsDummy"
//def credentialsId = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
def credentialsId = "d6796ef9-6d13-4515-bceb-bd2bdf41dad1"

node('Morpheus') {
    git credentialsId: "$credentialsId", url: "$url"
    def workspaceDir = pwd()
    
    //sh "cd jenkins-pipelines/wppCommon   ; ${mvnpath} clean versions:set -DnewVersion=${version}"
    sh "cd wppCommon   ; ${mvnpath} clean versions:set -DnewVersion=${version}"
    
    //sh "${mvnpath} -B -f ${workspaceDir}/jenkins-pipelines/wppCommon/pom.xml deploy cobertura:cobertura -Dcobertura.report.format=xml"
    sh "${mvnpath} -B -f ${workspaceDir}/wppCommon/pom.xml deploy cobertura:cobertura -Dcobertura.report.format=xml"
 
}
