package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppItem
import com.example.data.SmartFolder
import com.example.ui.components.OneUiAppIcon
import com.example.ui.theme.GalaxyCardBorder
import com.example.ui.theme.GalaxyCardDark
import com.example.ui.theme.OneUiBlueLight
import com.example.ui.theme.OneUiCyanGlow

@Composable
fun FolderDialog(
    folder: SmartFolder,
    allApps: List<AppItem>,
    onDismiss: () -> Unit,
    onLaunchApp: (AppItem) -> Unit,
    onRenameFolder: (String) -> Unit,
    onAddAppToFolder: (String) -> Unit,
    onRemoveAppFromFolder: (String) -> Unit,
    onDeleteFolder: () -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf(folder.name) }
    var showAddAppPicker by remember { mutableStateOf(false) }

    val folderApps = remember(folder.appPackageNames, allApps) {
        allApps.filter { it.packageName in folder.appPackageNames }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(32.dp))
                .background(GalaxyCardDark)
                .border(1.5.dp, GalaxyCardBorder, RoundedCornerShape(32.dp))
                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = OneUiCyanGlow)
                .padding(20.dp)
                .testTag("folder_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with editable folder name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditingName) {
                        OutlinedTextField(
                            value = folderNameInput,
                            onValueChange = { folderNameInput = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OneUiCyanGlow,
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("folder_rename_input")
                        )
                        IconButton(onClick = {
                            if (folderNameInput.isNotBlank()) {
                                onRenameFolder(folderNameInput)
                            }
                            isEditingName = false
                        }) {
                            Text("Save", color = OneUiCyanGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clickable { isEditingName = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = folder.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit name",
                                tint = OneUiCyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("folder_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Folder Apps Grid
                if (folderApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "This folder is empty.\nTap '+ Add Apps' below to add apps.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(folderApps) { app ->
                            Box(contentAlignment = Alignment.TopEnd) {
                                OneUiAppIcon(
                                    app = app,
                                    iconSize = 52.dp,
                                    showLabel = true,
                                    onClick = {
                                        onDismiss()
                                        onLaunchApp(app)
                                    },
                                    onLongClick = {
                                        onRemoveAppFromFolder(app.packageName)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Add App, Delete Folder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDeleteFolder,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF43F5E))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete Folder", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showAddAppPicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = OneUiBlueLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("folder_add_app_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Apps", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showAddAppPicker) {
        val availableApps = remember(allApps, folder.appPackageNames) {
            allApps.filter { it.packageName !in folder.appPackageNames }
        }

        AlertDialog(
            onDismissRequest = { showAddAppPicker = false },
            title = {
                Text("Add Apps to ${folder.name}", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableApps) { candidateApp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    onAddAppToFolder(candidateApp.packageName)
                                    showAddAppPicker = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OneUiAppIcon(
                                app = candidateApp,
                                iconSize = 36.dp,
                                showLabel = false,
                                onClick = {}
                            )
                            Text(
                                text = candidateApp.label,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = OneUiCyanGlow
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddAppPicker = false }) {
                    Text("Done", color = OneUiCyanGlow)
                }
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp)
        )
    }
}
