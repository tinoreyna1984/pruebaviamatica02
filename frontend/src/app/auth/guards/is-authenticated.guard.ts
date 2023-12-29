import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// verifica si está autenticado. Si no lo está, redirige al login para autenticar
export const isAuthenticatedGuard: CanActivateFn = (route, state) => {
  const authService = inject( AuthService );
  const router      = inject( Router );

  if(authService.isLoggedIn())
    return true;

  router.navigateByUrl('/auth/login');
  return false;
};

