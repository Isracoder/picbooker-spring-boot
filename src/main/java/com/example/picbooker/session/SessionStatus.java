package com.example.picbooker.session;

public enum SessionStatus {
    // to think of changing booked to approved
    BOOKED, CANCELED, APPROVAL_PENDING, RESCHEDULED, REFUSED
}
// 1st requested
// 2nd deposit pending if it needs
// 3rd booked

// to think what if I pay deposit directly when first booking ?
// to do rethink logic

// if something happens use canceled/rescheduled
// track if deposit paid with those