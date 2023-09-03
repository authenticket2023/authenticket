import * as React from 'react';
import dayjs, { Dayjs } from 'dayjs';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';


type DateChangeCallback = (newDate: Dayjs | null) => void;

interface BasicDatePickerProps {
  onDateChange: DateChangeCallback;
  value: Dayjs | null; // Add a value prop to control the DatePicker value from the parent component
  label: String;
}

export default function BasicDatePicker({ onDateChange, value, label}: BasicDatePickerProps) {
  const handleDatePickerChange = (newValue: Dayjs | null) => {
    onDateChange(newValue); // Call the parent's onDateChange prop directly here
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DateTimePicker
        label={label}
        format="YYYY-MM-DD HH:mm:ss"
        value={value}
        disablePast
        onChange={handleDatePickerChange} 
      />
    </LocalizationProvider>
  );
}





