<?php
require_once __DIR__ . '/../config.php';

$input = json_decode(file_get_contents("php://input"), true);

if (empty($input['email']) || empty($input['password']) || empty($input['fullName'])) {
    send_json(false, "Full name, email and password are required.", null, 400);
}

$fullName = trim($input['fullName']);
$email = trim(strtolower($input['email']));
$password = password_hash($input['password'], PASSWORD_BCRYPT);

try {
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        send_json(false, "Email address is already registered.", null, 400);
    }

    $stmt = $pdo->prepare("INSERT INTO users (full_name, email, password_hash) VALUES (?, ?, ?)");
    $stmt->execute([$fullName, $email, $password]);

    send_json(true, "User registered successfully.", ["id" => $pdo->lastInsertId(), "fullName" => $fullName, "email" => $email]);
} catch (Exception $e) {
    send_json(false, "Server error: " . $e->getMessage(), null, 500);
}
?>
