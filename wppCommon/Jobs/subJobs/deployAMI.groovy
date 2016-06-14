import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

//incoming parameters
//def url = "https://github.com/WPPg2/DevOps-Deployment"
//def gitCredentials = "8cf0000b-3991-4db0-a2d9-e157168d2cef"
//def serviceName="avreg"
//def serviceConfigPath = "/home/ec2-user/avregPipPilot/pipeline/services"
//def amiID = "ami-0a6f966a"
//def subnetID
//def subnetNum


def content
def content2
def slaveWorkspaceDir

node(targetNode)
{
	//get slave workspace directory
    slaveWorkspaceDir = pwd()
}
node('master'){
    //manipulate json file 1
    File inputFile = new File("${serviceConfigPath}/${serviceName}/pideploy-${serviceName}-input.json")
    content = inputFile.text
    def slurped = new JsonSlurper().parseText(content)
    def builder = new JsonBuilder(slurped)
    builder.content.stack.name = "pie-pod1-subnet${subnetNum}-${serviceName}"
    builder.content.stack.parameters[2].value = "${amiID}"
    builder.content.stack.parameters[4].value = "[\"subnet-${subnetID}\"]"
    builder.content.stack.template.path = "${slaveWorkspaceDir}/cloudformation/pie/templates/avatar-reg/AvatarReg.json"
    builder.content.log.temp_dir = "${slaveWorkspaceDir}"
    content = builder.toPrettyString()
	
    // null since non-serializable
    builder=null
}

node(targetNode) {
	//git checkout before any writeFile
    git credentialsId: "${gitCredentials}", url: "$url" 
	
    //writing manipulated json to slave node
    writeFile file: "pideploy-${serviceName}-input${subnetNum}.json", text: content
    }
node('master'){
    //fetching json file 2
    File inputFile = new File("${serviceConfigPath}/${serviceName}/wait-${serviceName}-input.json")
    content = inputFile.text
    def slurped = new JsonSlurper().parseText(content)
    def builder = new JsonBuilder(slurped)
    builder.content.stack.name = "pie-pod1-subnet${subnetNum}-${serviceName}"
    content = builder.toPrettyString()
	
    // null since non-serializable
    builder=null
}

node(targetNode){
    //writing manipulated json to slave node
    writeFile file: "wait-${serviceName}-input${subnetNum}.json", text: content
    }
node('master'){
    //fetching json file 3
    File inputFile = new File("${serviceConfigPath}/${serviceName}/getStackDetails-${serviceName}.json")
    content = inputFile.text
    def slurped = new JsonSlurper().parseText(content)
    def builder = new JsonBuilder(slurped)
    builder.content.stack.name = "pie-pod1-subnet${subnetNum}-${serviceName}"
    builder.content.property_file.name = "pie-pod1-subnet${subnetNum}-${serviceName}.outputs"
    builder.content.property_file.directory = "${slaveWorkspaceDir}"
    builder.content.log.name = "pie-pod1-subnet${subnetNum}-${serviceName}.log"
    builder.content.log.temp_dir = "${slaveWorkspaceDir}"
    content = builder.toPrettyString()
	
    // null since non-serializable
    builder=null
}

node(targetNode){
	//writing manipulated json to slave node
    writeFile file: "getStackDetails-${serviceName}${subnetNum}.json", text: content
        
    println "Deploying AvatarReg on 'PIE' stack. POD1-subnet1 with ami_id: ${amiID}"
    /*sh """cd ${slaveWorkspaceDir}/chef-repos/aws-manager-repo
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/pideploy-${serviceName}-input${subnetNum}.json -r 'recipe[cloudformation::updateStack]' -l info
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/wait-${serviceName}-input${subnetNum}.json  -r 'recipe[cloudformation::waitForStackReady]' -l info
    sudo chef-client -z -w -j ${slaveWorkspaceDir}/getStackDetails-${serviceName}${subnetNum}.json -r 'recipe[cloudformation::getStackOutputs]' -l info
    """*/
    def amiContent = "ami_id=${amiID}"
    writeFile file: "${serviceName}.log.amiid.tag", text: amiContent
}