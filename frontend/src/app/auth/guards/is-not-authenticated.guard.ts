import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// verifica si no está autenticado. Si lo está, redirige al panel principal
export const isNotAuthenticatedGuard: CanActivateFn = (route, state) => {
  const authService = inject( AuthService );
  const router      = inject( Router );

  if(authService.isLoggedIn())
  {
    router.navigateByUrl('/main');
    return false;
  }

  return true;
};
