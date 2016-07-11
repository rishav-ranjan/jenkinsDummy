import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

//incoming variables
//amiID
//targetNode
//gitCredentials
//url
def deployDate = new Date().format("ddMM")

node('master')
{
	masterWorkspace = pwd()
	getConfigFile(serviceConfigBaseURL,"tagging_ami_pie_input.json")
    //manipulating json file 1
    File inputFile = new File("${masterWorkspace}/tagging_ami_pie_input.json")
    content = inputFile.text
    def slurped = new JsonSlurper().parseText(content)
    def builder = new JsonBuilder(slurped)
    builder.content.aws.ami.id="${amiID}"
    builder.content.aws.ami.tags[1].value="${deployDate}"
    content = builder.toPrettyString()
    builder=null
}
node(targetNode)
{	
    slaveWorkspaceDir = pwd()
	//git checkout before writeFile
    git credentialsId: "$gitCredentials", url: "$url"   
    writeFile file: "tagging_ami_pie_input.json", text: content
	
	//run create AMI from instance recipe
    sh """cd ${slaveWorkspaceDir}/chef-repos/aws-manager-repo
    sudo chef-client -z -j ${slaveWorkspaceDir}/tagging_ami_pie_input.json -r 'recipe[ec2::tagAMI]' -l info"""
}

def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    sh """mkdir -p "${workspace}"
	"""
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}