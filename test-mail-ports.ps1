$host_name = "mail.technosprint.net"
$ports = @(993, 143, 587, 465, 25)

# DNS check
try {
    $ips = [System.Net.Dns]::GetHostAddresses($host_name)
    foreach ($ip in $ips) { Write-Host "DNS: $($ip.IPAddressToString)" }
} catch {
    Write-Host "DNS: FAILED to resolve $host_name"
}

# Port checks
foreach ($p in $ports) {
    try {
        $tcp = New-Object Net.Sockets.TcpClient
        $result = $tcp.BeginConnect($host_name, $p, $null, $null)
        $wait = $result.AsyncWaitHandle.WaitOne(5000, $false)
        if ($wait -and $tcp.Connected) {
            Write-Host "Port ${p}: OPEN"
        } else {
            Write-Host "Port ${p}: TIMEOUT"
        }
        $tcp.Close()
    } catch {
        Write-Host "Port ${p}: CLOSED - $($_.Exception.Message)"
    }
}

# Also test outlook.office365.com
Write-Host ""
Write-Host "Testing outlook.office365.com..."
try {
    $tcp = New-Object Net.Sockets.TcpClient
    $result = $tcp.BeginConnect("outlook.office365.com", 993, $null, $null)
    $wait = $result.AsyncWaitHandle.WaitOne(5000, $false)
    if ($wait -and $tcp.Connected) {
        Write-Host "outlook.office365.com:993 OPEN"
    } else {
        Write-Host "outlook.office365.com:993 TIMEOUT"
    }
    $tcp.Close()
} catch {
    Write-Host "outlook.office365.com:993 CLOSED"
}
