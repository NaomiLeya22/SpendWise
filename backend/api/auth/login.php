<?php
require_once __DIR__ . '/../config.php';

$input = json_decode(file_get_contents("php://input"), true);

if (empty($input['email']) || empty($input['password'])) {
    send_json(false, "Email and password are required.", null, 400);
}

$email = trim(strtolower($input['email']));
$password = $input['password'];

try {
    $stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user || !password_verify($password, $user['password_hash'])) {
        send_json(false, "Invalid email or password.", null, 401);
    }

    $token = bin2hex(random_bytes(32));

    send_json(true, "Login successful.", [
        "token" => $token,
        "user" => [
            "id" => (int)$user['id'],
            "fullName" => $user['full_name'],
            "email" => $user['email']
        ]
    ]);
} catch (Exception $e) {
    send_json(false, "Server error: " . $e->getMessage(), null, 500);
}
?>
