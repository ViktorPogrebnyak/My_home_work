param(
    [string]$JarPath = "$env:USERPROFILE/Downloads/oa_at_train_project-1.0-SNAPSHOT.jar/oa_at_train_project-1.0-SNAPSHOT.jar",
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $JarPath -PathType Leaf)) {
    throw "SmartShop JAR not found: $JarPath. Specify -JarPath."
}

$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
$staticPath = (Join-Path $PSScriptRoot 'static').Replace('\', '/') + '/'
$staticUri = [Uri]::new($staticPath).AbsoluteUri

& $java -jar $JarPath "--server.port=$Port" `
    "--spring.datasource.url=jdbc:h2:mem:smartshop-ui;DB_CLOSE_DELAY=-1" `
    "--spring.web.resources.static-locations=$staticUri,classpath:/static/"
exit $LASTEXITCODE
