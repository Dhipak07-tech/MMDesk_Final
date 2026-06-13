# Build Ticklora Spring Boot backend
$Root = $PSScriptRoot
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
$MvnBin = Join-Path $Root "maven\apache-maven-3.9.6\bin"
$JavaBin = Join-Path $env:JAVA_HOME "bin"
$env:Path = "$JavaBin;$MvnBin"

$TargetDir = Join-Path $Root "microservices\core-service-springboot"
Set-Location $TargetDir
Write-Host "Building ticklora-core..."
& "$MvnBin\mvn.cmd" package -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "BUILD SUCCESS"
} else {
    Write-Host "BUILD FAILED"
    exit 1
}
