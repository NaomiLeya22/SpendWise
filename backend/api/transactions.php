<?php
require_once __DIR__ . '/config.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    $userId = isset($_GET['userId']) ? (int)$_GET['userId'] : 1;
    $stmt = $pdo->prepare("SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC");
    $stmt->execute([$userId]);
    $rows = $stmt->fetchAll();

    $data = array_map(function($r) {
        return [
            "id" => (int)$r['id'],
            "clientUuid" => $r['client_uuid'],
            "type" => $r['type'],
            "category" => $r['category_name'],
            "amount" => (float)$r['amount'],
            "description" => $r['description'],
            "transactionDate" => $r['transaction_date']
        ];
    }, $rows);

    send_json(true, "Transactions retrieved.", $data);
} elseif ($method === 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);
    $userId = isset($input['userId']) ? (int)$input['userId'] : 1;

    if (empty($input['type']) || empty($input['amount']) || empty($input['transactionDate'])) {
        send_json(false, "Missing required fields (type, amount, transactionDate).", null, 400);
    }

    $clientUuid = $input['clientUuid'] ?? uniqid("tx_", true);
    $type = strtolower($input['type']);
    $category = $input['category'] ?? "General";
    $amount = (float)$input['amount'];
    $desc = $input['description'] ?? "";
    $date = $input['transactionDate'];

    $stmt = $pdo->prepare("INSERT INTO transactions (user_id, client_uuid, type, category_name, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount = VALUES(amount)");
    $stmt->execute([$userId, $clientUuid, $type, $category, $amount, $desc, $date]);
    $id = $pdo->lastInsertId();

    send_json(true, "Transaction synced successfully.", [
        "id" => (int)$id,
        "clientUuid" => $clientUuid,
        "type" => $type,
        "category" => $category,
        "amount" => $amount,
        "description" => $desc,
        "transactionDate" => $date
    ]);
}
?>
