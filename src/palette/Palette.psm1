Add-Type -AssemblyName PresentationFramework
[void] [System.Reflection.Assembly]::LoadWithPartialName("System.Windows.Forms")
[void] [System.Reflection.Assembly]::LoadWithPartialName("System.Windows")

function runScriptBlock([parameter(position = 0)] $scriptBlock) {
    try {
        &$scriptBlock
        exit 0
    }
    catch {
        write-error $_
        Write-Error ($_.InvocationInfo | Format-List -Force | Out-String) -ErrorAction Continue
        exit 1
    }
}

function LoadXaml($xaml) {
    Write-Host "Load Xaml" $xaml
    return [Windows.Markup.XamlReader]::Load((New-Object System.Xml.XmlNodeReader $xaml))
}

function BuildAppXaml {
    return [xml]@"
<Window
    xmlns="http://schemas.microsoft.com/winfx/2006/xaml/presentation"
    xmlns:x="http://schemas.microsoft.com/winfx/2006/xaml"
    x:Name="Window"
	Title="Available Actions"
	SizeToContent="WidthAndHeight"
	ResizeMode="CanMinimize"
	WindowStartupLocation="CenterScreen"
	MaxHeight="600"
	>
	<StackPanel Name="panelWindow" Margin="10">
	</StackPanel>
</Window>
"@
}

function InitApp($window) {
    # Build the tree view xaml
    $treeView = LoadXaml (BuildTreeViewXaml (Get-Item $Script:App.StrPaletteDir))
    InitTreeView $treeView

    $panelWindow = $window.findName("panelWindow")
    $panelWindow.Children.Add($treeView)

    # Keep the window on top
    $window.Add_Deactivated({
        $sender = $args[0]
        $sender.Topmost = $true;
    })
}

function BuildTreeViewXaml($file) {
    $children = (BuildFileNodeXaml $file)
    return [xml]@"
<TreeView
    xmlns="http://schemas.microsoft.com/winfx/2006/xaml/presentation"
    xmlns:x="http://schemas.microsoft.com/winfx/2006/xaml"
    Name="trvPalette"
    BorderThickness="0"
    FontSize="14">
    $($children)
</TreeView>
"@
}

function InitTreeView($treeView) {
    # expand the first node
    $treeView.Items[0].IsExpanded = $true

    # Add Palette Drag and Drop function
    $treeView.Add_MouseMove({
        $sender = $args[0]
        $e = $args[1]
        # write-host $sender
        # write-host $args

        if ($e.LeftButton -eq [System.Windows.Input.MouseButtonState]::Pressed) {
            write-host "copying" $sender.SelectedItem
            $dataFormat = [System.Windows.DataFormats]::FileDrop;
            $args = @($dataFormat, [System.String[]]@("C:\Users\pc\IdeaProjects\windows-context-menu-helper\dist\Palette\Command"))
            $dataObject = (new-object System.Windows.DataObject @($dataFormat, [System.String[]]@("C:\Users\pc\IdeaProjects\windows-context-menu-helper\dist\Palette\Command")))
            [System.Windows.DragDrop]::DoDragDrop($window, $dataObject, [System.Windows.DragDropEffects]::Copy);
        }
    })
}

function BuildLeafNodeXaml($file) {
    return @"
	<TreeViewItem>
		<TreeViewItem.Header>
	        <StackPanel Orientation="Horizontal">
	        	<Image Source="$($Script:App.SelfDir)\file20.png" />
                <TextBlock VerticalAlignment="Center" Text="$( $file.Name )"></TextBlock>
	        </StackPanel>
	    </TreeViewItem.Header>
	</TreeViewItem>
"@
}

function BuildContainerNodeXaml($file) {
    $childFiles = $file | Get-ChildItem -ErrorAction SilentlyContinue
    $children = ""
    foreach ($childFile in $childFiles) {
        # must check for null due to ps bug in some versions
        if (-not($childFile -eq $null)) {
            Write-Host "Found Child File" $childFile
            $xaml = BuildFileNodeXaml $childFile

            # concat the child xaml into the complete xaml of all the children
            $children += $xaml
        }
    }

    return @"
	<TreeViewItem>
	    <TreeViewItem.Header>
	        <StackPanel Orientation="Horizontal">
	        	 <Image Source="$($Script:App.SelfDir)\folder20.png" />
                <TextBlock VerticalAlignment="Center" Text="$( $file.Name )"></TextBlock>
	        </StackPanel>
	    </TreeViewItem.Header>
		$( $children )
	</TreeViewItem>
"@
}

function BuildFileNodeXaml($file) {
    write-host "Build File Node Xaml" $file
    if (test-path $file.FullName -PathType Container) {
        # this file is a directory
        Write-Host "Found Dir" $file
        return BuildContainerNodeXaml $file
    } else {
        # this file is a file
        Write-Host "Found File" $file
        return BuildLeafNodeXaml $file
    }
}

function LaunchApp($selfDir) {
    $scriptDir = Split-Path $script:MyInvocation.MyCommand.Path
    $script:App = @{
        SelfDir = $scriptDir;
        ProjectDir = $selfDir;
        AlwaysOnTop = $true;
        StrPaletteDir = "C:\Users\pc\IdeaProjects\windows-context-menu-helper\dist\Palette"
    }

    $window = LoadXaml (BuildAppXaml)
    InitApp $window
    $window.ShowDialog()
}