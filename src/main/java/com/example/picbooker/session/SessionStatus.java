package com.example.picbooker.session;

import lombok.Getter;

@Getter
public enum SessionStatus {
    BOOKED, CANCELED, DEPOSIT_PENDING, AWAITING_APPROVAL, RESCHEDULED
}
// 1st requested
// 2nd deposit pending if it needs
// 3rd booked

// to think what if I pay deposit directly when first booking ?
// to do rethink logic

// if something happens use canceled/rescheduled
// track if deposit paid with those