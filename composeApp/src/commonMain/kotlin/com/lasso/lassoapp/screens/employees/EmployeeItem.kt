package com.lasso.lassoapp.screens.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.pencil_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmployeeItem(
    employee: Employee,
    onEditClick: (Employee) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(LassoPrimary, Color(0xFF00B999))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.firstOrNull()?.uppercase() ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Name and Role
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = LassoTextPrimary
                    )
                )
                Text(
                    text = employee.role.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LassoTextMuted,
                        fontSize = 14.sp
                    )
                )
            }

            // Edit Action
            IconButton(onClick = { onEditClick(employee) }) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(Res.drawable.pencil_icon),
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
