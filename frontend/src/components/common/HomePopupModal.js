/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography, Box, Chip, IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import BugReportIcon from '@mui/icons-material/BugReport';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import { publicApi } from '../../api/services';

const DISMISSED_KEY = 'dismissedHomePopups';

const getDismissedIds = () => {
  try {
    return JSON.parse(localStorage.getItem(DISMISSED_KEY) || '[]');
  } catch {
    return [];
  }
};

const dismissPopup = (id) => {
  const ids = getDismissedIds();
  if (!ids.includes(id)) {
    localStorage.setItem(DISMISSED_KEY, JSON.stringify([...ids, id]));
  }
};

const typeMeta = {
  SUGGESTION: { color: 'info', label: 'Suggestion', icon: <LightbulbIcon /> },
  BUG: { color: 'error', label: 'Bug Notice', icon: <BugReportIcon /> },
  WARNING: { color: 'warning', label: 'Warning', icon: <WarningAmberIcon /> },
  OFFER: { color: 'success', label: 'Offer', icon: <LocalOfferIcon /> },
};

export default function HomePopupModal() {
  const [queue, setQueue] = useState([]);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    publicApi.activePopups()
      .then((list) => {
        const dismissed = getDismissedIds();
        const pending = (list || []).filter((p) => !dismissed.includes(p.id));
        setQueue(pending);
        setOpen(pending.length > 0);
      })
      .catch(() => {});
  }, []);

  const current = queue[0];

  const handleClose = () => {
    if (!current) return;
    dismissPopup(current.id);
    const rest = queue.slice(1);
    setQueue(rest);
    setOpen(rest.length > 0);
  };

  if (!current) return null;

  const meta = typeMeta[current.type] || typeMeta.SUGGESTION;

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{ sx: { borderRadius: 3, overflow: 'hidden' } }}
    >
      <DialogTitle sx={{ pr: 6, display: 'flex', alignItems: 'center', gap: 1 }}>
        <Box color={`${meta.color}.main`} display="flex">{meta.icon}</Box>
        <Box flexGrow={1}>
          <Chip size="small" color={meta.color} label={meta.label} sx={{ mb: 0.5 }} />
          <Typography variant="h6" component="div">{current.title}</Typography>
        </Box>
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{ position: 'absolute', right: 8, top: 8 }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-wrap' }}>
          {current.message}
        </Typography>
      </DialogContent>
      <DialogActions sx={{ px: 3, py: 2 }}>
        {queue.length > 1 && (
          <Typography variant="caption" color="text.secondary" sx={{ mr: 'auto' }}>
            {queue.length - 1} more notice{queue.length > 2 ? 's' : ''}
          </Typography>
        )}
        <Button variant="contained" onClick={handleClose}>
          Got it
        </Button>
      </DialogActions>
    </Dialog>
  );
}
