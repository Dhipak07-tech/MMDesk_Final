<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

use App\Http\Controllers\TicketController;

Route::post('/tickets', [TicketController::class, 'store']);
Route::patch('/tickets/{ticket}/status', [TicketController::class, 'updateStatus']);
Route::patch('/tickets/{ticket}/assign', [TicketController::class, 'assign']);
Route::post('/tickets/{ticket}/comments', [TicketController::class, 'comment']);

Route::post('/ai/suggest', [TicketController::class, 'suggest']);
Route::post('/ai/chat', [TicketController::class, 'chat']);
Route::post('/notify', [TicketController::class, 'notify']);
Route::post('/webhooks/whatsapp', [App\Http\Controllers\WebhookController::class, 'whatsapp']);
